import React, {useState, useRef, useEffect} from "react";
import { StyleSheet, View, Text, Image, NativeModules, AppState, Button, TouchableOpacity } from "react-native";
import AppIntroSlider from "react-native-app-intro-slider";
import * as Constants from '../util/constants';
import { SafeAreaProvider } from 'react-native-safe-area-context';

import UsageScreen from "./UsageScreen";


const IntroScreen = () => {

  const [usageStatsPermission, setUsageStatsPermission] = useState(false);
  const [ignoreBatteryOptimizationPermission, setIgnoreBatteryOptimizationPermission] = useState(false);
  const appState = useRef(AppState.currentState);
  const [appStateVisible, setAppStateVisible] = useState(appState.current);

  const getUsageStatsPermission = () => {
    NativeModules.Permissions.getUsageStatsPermissions();
  }

  const getIgnoreBatteryOptimizationPermission = () => {
    NativeModules.Permissions.getIgnoreBatteryOptimizationPermission();
  }

  const checkingPermissions = async () => {
    try{
      const p1 = await NativeModules.Permissions.checkUsageStatsPermissions();
      const p2 = await NativeModules.Permissions.checkIgnoreBatteryOptimizationPermission();

      setUsageStatsPermission(p1);
      setIgnoreBatteryOptimizationPermission(p2);

    } catch(error) {
      console.log(error);
    } 
  }

  useEffect(async () => {
    console.log("Intro Screen Starts!");
    await checkingPermissions();
  },[]);

  useEffect(() => {
    AppState.addEventListener("change", _handleAppStateChange);

    return () => {
      AppState.removeEventListener("change", _handleAppStateChange);
    };
  }, []);

  const _handleAppStateChange = async (nextAppState) => {
    if (appState.current.match(/inactive|background/) && nextAppState === "active") {
      console.log("App has come to the foreground!");
      await checkingPermissions();
    }

    appState.current = nextAppState;
    setAppStateVisible(appState.current);
    console.log("AppState", appState.current);
  };


  const _renderButton = (buttonText, onPress, permissionStatus) => {
    return (
      <TouchableOpacity onPress={onPress} style={{paddingTop: 15}}>
        <View style={styles.button}>
          <Text style={styles.buttonText} >{buttonText} {permissionStatus ? Constants.PERMISSION_GIVEN : Constants.PERMISSION_PENDING}</Text>
        </View>
      </TouchableOpacity>
    );
  }

  const _renderButtonInfo = (buttonText) => {
    return (
      <View style={styles.buttonInfo}>
      <Text style={styles.buttonInfoText} >{buttonText}</Text>
    </View>
    );
  }

  const _renderItem = ({ item }) => {
    return (
      <View style={[styles.slide, item.backgroundColor]}>
        <Text style={styles.title}>{item.title}</Text>
        <Image source={item.image} style={styles.image}/>
        {item.permissionScreen ? <></> : <Text style={styles.text}>{item.text}</Text>}
        {item.button1 ? _renderButton(item.button1, getUsageStatsPermission, usageStatsPermission) : <></>}
        {item.button2 ? _renderButton(item.button2, getIgnoreBatteryOptimizationPermission, ignoreBatteryOptimizationPermission) : <></>}
        {item.button3 ? _renderButtonInfo(item.button3) : <></>}
      </View>
    );

  }

  return (
    <View style={{flex: 1}}>
      <SafeAreaProvider>
        {(usageStatsPermission&&ignoreBatteryOptimizationPermission) == false ? 
        <AppIntroSlider
          renderItem={_renderItem}
          data={Constants.introScreenData}
          renderDoneButton={()=><></>}/> :
        <UsageScreen/>}
      </SafeAreaProvider>
    </View>
  );
}

export default IntroScreen;





const styles = StyleSheet.create({
  slide: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    // padding:20
    // backgroundColor: 'blue',
  },
  image: {
    width: Constants.SCREEN_WIDTH * 0.7,
    height: Constants.SCREEN_WIDTH * 0.7,
    marginVertical: Constants.SCREEN_HEIGHT * 0.05,
  },
  title: {
    fontSize: 20,
    fontWeight:"bold",
    color: 'white',
    textAlign: 'center',
    fontFamily: "monospace",
  },
  text: {
    color: 'rgba(255, 255, 255, 1)',
    fontSize: 15,
    fontFamily: "monospace",
    textAlign: 'center',
    paddingLeft: Constants.SCREEN_WIDTH * 0.1,
    paddingRight: Constants.SCREEN_WIDTH * 0.1,
    fontWeight: "bold"
  },
  button: {
    width: Constants.SCREEN_WIDTH * 0.8,
    backgroundColor: 'rgba(0, 0, 0, .3)',
    alignItems: 'center',
    justifyContent: 'center',
  },
  buttonText: {
    color: 'white',
    fontSize: 18,
    padding: 10,
  },
  buttonInfo: {
    width: Constants.SCREEN_WIDTH * 0.8,
    // backgroundColor: 'rgba(0, 0, 0, .3)',
  },
  buttonInfoText: {
    color: 'white',
    fontSize: 14,
  },
});