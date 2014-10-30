package com.techq.test;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * @author CHQ
 * @since  2013-2-13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { 
		"classpath:/applicationContext-aop-base.xml",
        "classpath:/applicationContext-bo.xml"
         })
public class BaseTestCase  {

}
