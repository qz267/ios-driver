/*
 * Copyright 2012 ios-driver committers.
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

package org.uiautomation.ios.server.application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.uiautomation.ios.exceptions.IOSAutomationException;

public class IOSApplication {

  private final File app;
  private final Localizable currentLanguage;
  private final List<LanguageDictionary> dictionaries = new ArrayList<LanguageDictionary>();


  /**
   * 
   * @param currentLanguage
   * @param pathToApp
   * @throws IOSAutomationException
   */
  public IOSApplication(Localizable currentLanguage, String pathToApp)
      throws IOSAutomationException {
    this.app = new File(pathToApp);
    if (!app.exists()) {
      throw new IOSAutomationException(pathToApp + "isn't an IOS app.");
    }
    this.currentLanguage = currentLanguage;
  }


  /**
   * get the list of languages the application if localized to.
   * 
   * @return
   * @throws Exception
   */
  public List<Localizable> getSupportedLanguages() throws Exception {
    List<Localizable> res = new ArrayList<Localizable>();
    List<File> l10ns = LanguageDictionary.getL10NFiles(app);
    for (File f : l10ns) {
      String name = LanguageDictionary.extractLanguageName(f);
      res.add(new LanguageDictionary(name).getLanguage());
    }
    return res;
  }



  public LanguageDictionary getDictionary(Localizable language) throws IOSAutomationException {
    for (LanguageDictionary dict : dictionaries) {
      if (dict.getLanguage() == language) {
        return dict;
      }
    }
    throw new IOSAutomationException("Cannot find dictionary for " + language);
  }



  /**
   * Load all the dictionaries for the application.
   * 
   * @throws Exception
   */
  public void loadAllContent() throws IOSAutomationException {
    if (!dictionaries.isEmpty()) {
      throw new IOSAutomationException("Content already present.");
    }
    List<File> l10nFiles = LanguageDictionary.getL10NFiles(app);
    for (File f : l10nFiles) {
      LanguageDictionary dict;
      try {
        dict = LanguageDictionary.createFromFile(f);
      } catch (Exception e) {
        throw new IOSAutomationException("Cannot load language translation for " + f, e);
      }
      dictionaries.add(dict);
    }
  }



  public String translate(ContentResult res, Localizable language) throws IOSAutomationException {
    LanguageDictionary destinationLanguage = getDictionary(language);
    return destinationLanguage.translate(res);

  }

  public JSONObject getTranslations(String name) throws JSONException {

    JSONObject l10n = new JSONObject();
    l10n.put("matches", 0);
    if (name != null && !name.isEmpty() && !"null".equals(name)) {
      try {
        List<ContentResult> results = getPotentialMatches(name);

        l10n.put("matches", results.size());
        for (Localizable language : getSupportedLanguages()) {
          JSONArray possibleMatches = new JSONArray();
          for (ContentResult res : results) {
            possibleMatches.put(translate(res, language));
          }
          l10n.put(language.getName(), possibleMatches);
        }

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return l10n;
  }


  private List<ContentResult> getPotentialMatches(String name) throws IOSAutomationException {
    LanguageDictionary dict = getDictionary(currentLanguage);
    List<ContentResult> res = dict.getPotentialMatches(name);
    return res;
  }


  public void addDictionary(LanguageDictionary dict) {
    dictionaries.add(dict);
  }
}