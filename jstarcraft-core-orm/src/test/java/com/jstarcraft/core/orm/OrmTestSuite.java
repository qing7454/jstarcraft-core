package com.jstarcraft.core.orm;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jstarcraft.core.orm.berkeley.BerkeleyTestSuite;
import com.jstarcraft.core.orm.hibernate.HibernateTestSuite;
import com.jstarcraft.core.orm.identification.IdentityTestSuite;
import com.jstarcraft.core.orm.mongo.MongoTestSuite;
import com.jstarcraft.core.orm.mybatis.MyBatisAccessorTestCase;
import com.jstarcraft.core.orm.neo4j.Neo4jTestSuite;

@RunWith(Suite.class)
@SuiteClasses({ IdentityTestSuite.class, BerkeleyTestSuite.class, HibernateTestSuite.class, MongoTestSuite.class, MyBatisAccessorTestCase.class, Neo4jTestSuite.class })
public class OrmTestSuite {

}
