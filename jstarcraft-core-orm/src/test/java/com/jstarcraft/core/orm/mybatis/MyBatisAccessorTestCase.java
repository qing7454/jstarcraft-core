package com.jstarcraft.core.orm.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jstarcraft.core.orm.OrmCondition;
import com.jstarcraft.core.orm.OrmPagination;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class MyBatisAccessorTestCase {

    @Autowired
    private MyBatisAccessor accessor;

    @Test
    public void testCRUD() throws Exception {
        int size = 100;

        for (int index = 0; index < size; index++) {
            // 创建对象并保存
            MockObject object = MockObject.instanceOf(index, "birdy", "mickey" + index, index, MockEnumeration.RANDOM);
            accessor.create(MockObject.class, object);
            int id = object.getId();
            Assert.assertThat(id, CoreMatchers.equalTo(index));

            // 获取对象并比较
            MockObject instance = accessor.get(MockObject.class, id);
            Assert.assertThat(instance, CoreMatchers.equalTo(object));

            // 修改对象并保存
            object.setName("mickey");
            accessor.update(MockObject.class, object);
            instance = accessor.get(MockObject.class, id);
            Assert.assertThat(instance, CoreMatchers.equalTo(object));
        }

        // 查询对象的最大标识
        int maximum = accessor.maximumIdentity(MockObject.class, -size, size);
        Assert.assertThat(maximum, CoreMatchers.equalTo(size - 1));
        // 查询对象的最小标识
        int minimum = accessor.minimumIdentity(MockObject.class, -size, size);
        Assert.assertThat(minimum, CoreMatchers.equalTo(0));

        // 查询指定范围的主键与对象
        Map<Integer, Object> id2Moneys = accessor.queryIdentities(MockObject.class, OrmCondition.All, "money");
        Assert.assertThat(id2Moneys.size(), CoreMatchers.equalTo(size));
        List<MockObject> objects = accessor.queryInstances(MockObject.class, OrmCondition.All, "money");
        for (MockObject object : objects) {
            Assert.assertThat(object.getMoney(), CoreMatchers.equalTo(id2Moneys.get(object.getId())));
        }

        id2Moneys = accessor.queryIdentities(MockObject.class, OrmCondition.Equal, "money", 0);
        Assert.assertThat(id2Moneys.size(), CoreMatchers.equalTo(1));
        objects = accessor.queryInstances(MockObject.class, OrmCondition.Equal, "money", 0);
        for (MockObject object : objects) {
            Assert.assertThat(object.getMoney(), CoreMatchers.equalTo(id2Moneys.get(object.getId())));
        }

        id2Moneys = accessor.queryIdentities(MockObject.class, OrmCondition.Between, "money", 1, 50);
        Assert.assertThat(id2Moneys.size(), CoreMatchers.equalTo(50));
        objects = accessor.queryInstances(MockObject.class, OrmCondition.Between, "money", 1, 50);
        for (MockObject object : objects) {
            Assert.assertThat(object.getMoney(), CoreMatchers.equalTo(id2Moneys.get(object.getId())));
        }

        id2Moneys = accessor.queryIdentities(MockObject.class, OrmCondition.In, "money", 25, 50, 75);
        Assert.assertThat(id2Moneys.size(), CoreMatchers.equalTo(3));
        objects = accessor.queryInstances(MockObject.class, OrmCondition.In, "money", 25, 50, 75);
        for (MockObject object : objects) {
            Assert.assertThat(object.getMoney(), CoreMatchers.equalTo(id2Moneys.get(object.getId())));
        }

        Map<String, Object> condition = new HashMap<>();
        condition.put("race", MockEnumeration.RANDOM);
        condition.put("id", 0);

        // 查询分页
        OrmPagination pagination = new OrmPagination(1, 15);
        objects = accessor.query(MockObject.class, pagination);
        Assert.assertTrue(objects.size() == 15);
        AtomicInteger times = new AtomicInteger();
        accessor.iterate((object) -> {
            times.incrementAndGet();
        }, MockObject.class, pagination);
        Assert.assertTrue(times.get() == 15);

        pagination = new OrmPagination(7, 15);
        objects = accessor.query(MockObject.class, pagination);
        Assert.assertTrue(objects.size() == 10);
        times.set(0);
        accessor.iterate((object) -> {
            times.incrementAndGet();
        }, MockObject.class, pagination);
        Assert.assertTrue(times.get() == 10);

        // 测试总数
        long count = accessor.count(MockObject.class);
        Assert.assertTrue(count == size);
        objects = accessor.query(MockObject.class, null);
        Assert.assertTrue(objects.size() == count);

        count = accessor.countIntersection(MockObject.class, condition);
        Assert.assertTrue(count == 1);
        objects = accessor.queryIntersection(MockObject.class, condition, null);
        Assert.assertTrue(objects.size() == count);

        count = accessor.countUnion(MockObject.class, condition);
        Assert.assertTrue(count == size);
        objects = accessor.queryUnion(MockObject.class, condition, null);
        Assert.assertTrue(objects.size() == count);

        // 删除对象并保存
        for (MockObject object : accessor.queryIntersection(MockObject.class, condition, null)) {
            accessor.delete(MockObject.class, object);
            object = accessor.get(MockObject.class, object.getId());
            Assert.assertNull(object);
        }
        for (MockObject object : accessor.queryUnion(MockObject.class, condition, null)) {
            accessor.delete(MockObject.class, object.getId());
            object = accessor.get(MockObject.class, object.getId());
            Assert.assertNull(object);
        }
    }

}
