package com.ebay.data.utils.spreadsheet;

import org.apache.commons.lang.StringUtils;

public class TestObject {
	public static final String TEST_CASE_ID = "TestObject.TestCaseId";
	public static final String TEST_METHOD = "TestObject.TestMethod";
	public static final String TEST_TITLE = "TestObject.TestTitle";
	public static final String TEST_SITE = "TestObject.TestSite";

	private String testCaseId = "";
	private String testMethod = "";
	private String testTitle = "";
	private String testSite = "";

	public String getTestCaseId() {
		return testCaseId;
	}

	public String getTestMethod() {
		return testMethod;
	}

	public String getTestSite() {
		return testSite;
	}

	public String getTestTitle() {
		return testTitle;
	}

	public void setTestCaseId(String testCaseId) {
		this.testCaseId = testCaseId;
	}

	public void setTestMethod(String testMethod) {
		this.testMethod = testMethod;
	}

	public void setTestSite(String testSite) {
		this.testSite = testSite;
	}

	public void setTestTitle(String testTitle) {
		this.testTitle = testTitle;
	}

	public String toString() {
		StringBuffer ret = new StringBuffer();
		ret.append("[TestCaseId=" + testCaseId);
		if (StringUtils.isNotEmpty(testMethod)) {
			ret.append("|TestMethod=" + testMethod);
		}
		if (StringUtils.isNotEmpty(testTitle)) {
			ret.append("|TestTitle=" + testTitle);
		}
		if (StringUtils.isNotEmpty(testSite)) {
			ret.append("|Site=" + testSite);
		}
		ret.append("]");
		return ret.toString();
	}

}
