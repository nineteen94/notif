package com.rn_episode1;

import com.facebook.react.ReactActivity;
import android.os.Bundle;

public class MainActivity extends ReactActivity {

  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  public static String PACKAGE_NAME;

  @Override
  protected String getMainComponentName() {
    return "rn_episode1";
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    PACKAGE_NAME = getApplicationContext().getPackageName();
  }



}



















