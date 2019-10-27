package com.chuanglan.data.service.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.chuanglan.mongo.service.Application;
import com.chuanglan.mongo.service.util.ThreadLocalContainerUtil;

import junit.framework.TestCase;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class ThreadLocalContainerUtilTest {

	@Autowired
	private ThreadLocalContainerUtil threadUtil; 

	@Test
	public void testPutDocumentName() {
		String documentName = "test-doc";
		threadUtil.putDocumentName(documentName);
		TestCase.assertEquals(documentName, threadUtil.getDocumentName());
	}

	@Test
	public void testGetDocumentName() {
		String documentName = "test-doc";
		threadUtil.putDocumentName(documentName);
		TestCase.assertEquals(documentName, threadUtil.getDocumentName());
	}

	@Test
	public void testClearDocudmentName() {
		String documentName = "test-doc";
		threadUtil.putDocumentName(documentName);
		threadUtil.clearDocudmentName();
		TestCase.assertEquals("common_message", threadUtil.getDocumentName());
	}

}
