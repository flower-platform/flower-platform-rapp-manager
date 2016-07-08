package org.flowerplatform.rapp_manager.linux.test;

import org.junit.runner.RunWith;

import com.googlecode.junittoolbox.SuiteClasses;
import com.googlecode.junittoolbox.WildcardPatternSuite;

@RunWith(WildcardPatternSuite.class)
@SuiteClasses({"**/*.class", "!*TestSuiteLinux.class"})
public class TestSuiteLinux {

}
