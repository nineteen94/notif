import React, {useState, useRef, useEffect} from "react";
import { 
  StyleSheet, 
  Text, 
  Image, 
  KeyboardAvoidingView, 
  TouchableWithoutFeedback, 
  Platform, 
  View,
  StatusBar,
  TouchableOpacity,
  Dimensions,
  NativeModules,
  AppState
} from 'react-native';

import * as Constants from '../util/constants';
import { Form } from "@unform/mobile";

import UserInfoPicker from "./common/UserInfoPicker";
import UserInfoInput from "./common/UserInfoInput";
import UsageScreen from "./UsageScreen";


const UserInfoSceen = () => {

  const formRef = useRef(null);

  let userName = useRef("");
  let userPronoun = useRef("");

  const [isUserInfoAvailable, setIsUserInfoAvailable] = useState(false);

  const appState = useRef(AppState.currentState);
  const [appStateVisible, setAppStateVisible] = useState(appState.current);

  const checkingUserInfo = async () => {
    try{
      const p1 = await NativeModules.RoomDB.isUserInfoAvailable();

      userName.current = await NativeModules.RoomDB.getUserName();
      userPronoun.current = await NativeModules.RoomDB.getUserPronoun();

      setIsUserInfoAvailable(p1);

    } catch(error) {
      console.log(error);
    } 
  }

  useEffect(async () => {
    console.log("User Info Screen Starts!");
    await checkingUserInfo();
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
      await checkingUserInfo();
    }

    appState.current = nextAppState;
    setAppStateVisible(appState.current);
    console.log("AppState", appState.current);
  };

  const getName = (str) => {
    const strSplit = str.split(" ");
    let i = 0;
    while(i < strSplit.length && strSplit[i].length == 0){
      i ++;
    }
    if(i == strSplit.length) {
      return "";
    } else {
      return strSplit[i];
    }
  }

  const handleSubmit = (data) => {

    userName.current = getName(data.name);
    userPronoun.current = data.pronoun;

    if(userName.current.length && userPronoun.current.length) {
      console.log(data);

      var nameArray = userName.current.split(" ");

      userName.current = nameArray[0];

      NativeModules.RoomDB.setUserInfo(userName.current, userPronoun.current);
      
      setIsUserInfoAvailable(true);
    } else {
      console.log("Something is missing");
    }
  }

  const pickerOptions = [
    { value: 'he', label: 'He' },
    { value: 'she', label: 'She' },
  ];

  const editInfo = () => {
    setIsUserInfoAvailable(false);
  }

  return (
    <View style={{flex: 1}}>
      {isUserInfoAvailable == false ? 
      
      <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : null} style={styles.container}>
        <View>

          <Form ref={formRef} onSubmit={handleSubmit} initialData={ userName.current.length&&userPronoun.current.length ? {name: userName.current, pronoun: userPronoun.current} : {}} >
            <Image 
              style={styles.logo} 
              source={require('../assets/Fourth.webp')}
            />

            <UserInfoInput 
              name="name" 
              label="Who are you?"
              placeholder="Your knick name or first name"
              />

            <UserInfoPicker name="pronoun" items={pickerOptions} />

            <TouchableOpacity style={styles.submitButton} onPress={() => formRef.current.submitForm()}>
              <Text style={styles.submitButtonText}>Ok</Text>
            </TouchableOpacity>

          </Form>
        </View>
      </KeyboardAvoidingView>

      : <UsageScreen userName={userName.current} userPronoun={userPronoun.current} editInfo={editInfo}/>}

    </View>
  );

}

export default UserInfoSceen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'stretch',
    padding: 20,
    backgroundColor: Constants.COLOR_1
  },

  logo: {
    width: Constants.SCREEN_WIDTH * 0.7,
    height: Constants.SCREEN_WIDTH * 0.7,
    resizeMode: 'contain',
    alignSelf: 'center',
  },

  submitButton: {
    backgroundColor: '#111',
    borderRadius: 10,
    padding: 16,
    alignItems: 'center'
  },

  submitButtonText: {
    fontWeight: 'bold',
    color: '#fff',
    fontSize: 15,
  },
});