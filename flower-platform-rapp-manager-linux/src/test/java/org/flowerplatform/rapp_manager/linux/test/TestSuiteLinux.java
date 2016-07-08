package org.flowerplatform.rapp_manager.linux.test;

import org.flowerplatform.rapp_manager.linux.Constants;
import org.flowerplatform.rapp_manager.test.util.TestUtil;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.googlecode.junittoolbox.SuiteClasses;
import com.googlecode.junittoolbox.WildcardPatternSuite;

@RunWith(WildcardPatternSuite.class)
@SuiteClasses({"**/*.class", "!*TestSuiteLinux.class"})
public class TestSuiteLinux {

	@BeforeClass
	public static void init() throws ReflectiveOperationException, SecurityException {
		TestUtil.modifyFieldValue(Constants.class, "BIN_PATH", "./src_debian_package/opt/flower-platform/bin", null);
	    System.setProperty("user.home", "./target/test-classes");
	}

}
