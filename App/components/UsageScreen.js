import React, { useEffect, useState, useRef } from 'react';
import {View, NativeModules, ActivityIndicator, AppState, ScrollView, TouchableWithoutFeedback, StyleSheet} from 'react-native';
import {Avatar, Text, Button} from 'react-native-elements';
import DropDownPicker from 'react-native-dropdown-picker';

import { APPMODEL_APPNAME, APPMODEL_AVERAGEUSAGE, APPMODEL_HISTORICALUSAGE, APPMODEL_ISMONITORED, APPMODEL_PACKAGENAME, APPMODEL_URI, APPMODEL_WEEKDAYSUSAGE, DROPDOWN_NUMAPPS, SCREEN_HEIGHT } from '../util/constants';
import * as Constants from '../util/constants';

import DividerSpace from './common/DividerSpace';
import ApplicationCardsAll from './ApplicationCardsAll';
import RenderBadgeItem from './others/RenderBadgeItem';
import NoAppSelected from './others/NoAppSelected';
import Tabs from './Tabs';
import LifeToolTip from './common/LifeToolTip';

const UsageScreen = ({userName, userPronoun, editInfo}) => {

  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(false);

  const [open, setOpen] = useState(false);
  const [value, setValue] = useState([]);
  const [items, setItems] = useState([]);

  const appState = useRef(AppState.currentState);
  const [appStateVisible, setAppStateVisible] = useState(appState.current);

  let appDatabase = useRef(null);
  let applicationCardMaps = useRef();
  let dayOfTheWeek = useRef();

  useEffect(async () => {

    console.log("Usage Screen Starts!");

    await initializeUsageScreen();

  }, []);

  useEffect(() => {

    AppState.addEventListener("change", _handleAppStateChange);
    
    return () => {
    
      AppState.removeEventListener("change", _handleAppStateChange);
    };
  }, [] );

  const _handleAppStateChange = async (nextAppState) => {

    if (appState.current.match(/inactive|background/) && nextAppState == "active") {
      
      await initializeUsageScreen();
    } 

    appState.current = nextAppState;

    setAppStateVisible(appState.current);
  };

  const initializeUsageScreen = async () => {

    try {

      await NativeModules.NotificationModule.startOneTimeWork();

      appDatabase.current = await NativeModules.RoomDB.loadData();

      dayOfTheWeek.current = appDatabase.current.pop()[Constants.APPMODEL_DAYOFTHEWEEK] - 1;

      const dropdownValuePlaceholder = [];

      const dropdownItemsPlaceholder = [];

      applicationCardMaps.current = new Map();

      appDatabase.current.sort((a,b) => b[APPMODEL_AVERAGEUSAGE] - a[APPMODEL_AVERAGEUSAGE]);

      for(let i = 0; i < appDatabase.current.length && i < DROPDOWN_NUMAPPS; i ++) {

        const appObject = appDatabase.current[i];

        const packageName = appObject[APPMODEL_PACKAGENAME];

        const appName = appObject[APPMODEL_APPNAME];

        const appWeekDaysUsage = appObject[APPMODEL_WEEKDAYSUSAGE];

        const appHistoricalUsage = appObject[APPMODEL_HISTORICALUSAGE];

        const appAverageUsage = appObject[APPMODEL_AVERAGEUSAGE];

        const appIconURI = appObject[APPMODEL_URI];

        const appIsMontiored = appObject[APPMODEL_ISMONITORED];

        const appThisWeekUsage = appObject[Constants.APPMODEL_THISWEEKUSAGE];

        const appMap = new Map();
        
        appMap.set(APPMODEL_APPNAME, appName);

        appMap.set(APPMODEL_WEEKDAYSUSAGE, appWeekDaysUsage);

        appMap.set(APPMODEL_HISTORICALUSAGE, appHistoricalUsage);

        appMap.set(APPMODEL_AVERAGEUSAGE, appAverageUsage);

        appMap.set(APPMODEL_URI, appIconURI);

        appMap.set(Constants.APPMODEL_THISWEEKUSAGE, appThisWeekUsage);

        applicationCardMaps.current.set(packageName, appMap);

        dropdownItemsPlaceholder.push({
          "label": appName,
          "value": packageName,
          "icon" : () => <Avatar rounded source={{uri: appIconURI}}/>
        });

        appIsMontiored == true && dropdownValuePlaceholder.push(packageName);        
      }

      setItems(dropdownItemsPlaceholder);

      setValue(dropdownValuePlaceholder);

      setError(false);

    } catch(error) {
      setError(true); 
      console.log("aaa"  +error);
    } finally {
      setIsLoading(false);
    }
  }

  const appSelectHandler = async (value) => { 
    try {

      await NativeModules.RoomDB.setAppsToMonitor(value);
    
    } catch (error) {
      console.log('ERROR IN CHANGING SELECTED APPS' + error);
    }
  }

  const closeDropDown = () => {
    open == true && setOpen(false);
  }

  const toggleDropDown = () => setOpen(!open);

  const sumReducer = (previousValue, currentValue) => previousValue + currentValue;

  //👋 👨

  return (
    <View style={styles.container}>
    {isLoading == true ? <ActivityIndicator style={{flex: 1, justifyContent: "center"}} size="large" color="#0000ff"/> : (
      <>
      {error == true ? <Text>Something went wrong</Text> : (

        <View style={styles.innerContainer}>

          <View>

            <TouchableWithoutFeedback onPress={closeDropDown} onLongPress={editInfo}>
            <View style={styles.introContainer}>
              <Text adjustsFontSizeToFit>
                <Text style={styles.introText} >Hey {userName} 🖖<Text style={styles.introSubText}> ({userPronoun})</Text></Text>
              </Text>
            </View>
            </TouchableWithoutFeedback>

            <Button 
            title="SELECT APPS"
            buttonStyle={{backgroundColor: Constants.COLOR_BUTTON}}
            titleStyle={{fontFamily:"monospace", fontWeight:'bold'}}
            onPress={toggleDropDown}
            />

            <View >
            <DropDownPicker
              zIndex={2}
              listMode="SCROLLVIEW"
              placeholder=""
              multiple={true}
              items={items}
              ite
              value={value}
              maxHeight={300}
              open={open}
              setOpen={setOpen}
              setValue={setValue}
              onChangeValue={appSelectHandler}

              mode="BADGE"
              showBadgeDot={false}

              renderBadgeItem={RenderBadgeItem}

              showArrowIcon={false}

              selectedItemLabelStyle={{
                fontWeight: "bold",
              }}

              // textStyle={{fontFamily:"monospace"}}

              badgeColors={[Constants.COLOR_1]} 

              style={{
                borderColor: "#c7c7c7",
                borderTopWidth:0,
                backgroundColor: Constants.COLOR_1
              }}

              dropDownContainerStyle={{
                borderColor:"#c7c7c7",
                borderTopWidth:0,
                backgroundColor: Constants.COLOR_1,
              }}

            />
            </View>
          </View>


          {value.length == 0 ? 
          <NoAppSelected closeDropDown={closeDropDown}/> : (

          <ScrollView>
          <TouchableWithoutFeedback onPress={closeDropDown}>
          <View style={styles.scrollView}>

            <DividerSpace width={10}/>
            
            <Tabs 
            width={Constants.SCREEN_WIDTH*0.98}
            dayOfTheWeek={dayOfTheWeek.current}
            todayUsage={value.map(packageName => applicationCardMaps.current.get(packageName).get(APPMODEL_WEEKDAYSUSAGE)[dayOfTheWeek.current]).reduce(sumReducer)}
            thisWeekUsage={value.map(packageName => applicationCardMaps.current.get(packageName).get(Constants.APPMODEL_THISWEEKUSAGE)).reduce(sumReducer)}
            last30DaysUsage={value.map(packageName => applicationCardMaps.current.get(packageName).get(APPMODEL_HISTORICALUSAGE)).reduce(sumReducer)}
            />

            <DividerSpace width={20}/>

            <LifeToolTip />

            <ApplicationCardsAll 
            value={value}
            applicationCardMaps={applicationCardMaps}
            />

          </View>
          </TouchableWithoutFeedback>
          </ScrollView>

          )}



        </View>

      )}
      </>
    )}
    </View>
  );

}

const styles = StyleSheet.create({

  introContainer: {
    height: SCREEN_HEIGHT*0.07,
    backgroundColor: Constants.COLOR_1,
    paddingBottom: 5,
    borderColor: "grey",
    // borderBottomWidth: 2,
    padding: 10
  },

  introText: {
    fontSize: 40,
    fontFamily: "sans-serif-light",
    fontWeight: "bold"
  },

  introSubText: {
    fontSize: 30,
    fontStyle: "italic",
    fontWeight: "normal",
  },

  container: {
    paddingTop: 0,
    alignItems: "center",
    backgroundColor: Constants.COLOR_1,
    height: Constants.SCREEN_HEIGHT
  },

  innerContainer: {
    zIndex: 3, 
    // width:"98%",
  },

  scrollView: {
    paddingBottom:200,  
  }

})

export default UsageScreen;