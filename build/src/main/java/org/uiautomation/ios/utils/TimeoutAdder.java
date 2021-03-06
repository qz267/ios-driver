/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.uiautomation.ios.utils;


import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.xml.XmlTest;

import java.util.List;

public class TimeoutAdder implements ISuiteListener {

  @Override
  public void onStart(ISuite suite) {
    suite.getXmlSuite().setTimeOut("60000");
    List<XmlTest> l = suite.getXmlSuite().getTests();
    for (XmlTest test : l) {
      test.setPreserveOrder("true");
    }
  }

  @Override
  public void onFinish(ISuite suite) {

  }
}
