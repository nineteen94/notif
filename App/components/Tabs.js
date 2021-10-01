import React, {useState, useEffect} from 'react';
import {View, Text, StyleSheet} from 'react-native';
import { TabView, SceneMap, TabBar} from 'react-native-tab-view';
import { COLOR_1, COLOR_2, COLOR_3, COLOR_4, LIFE_AVAILABLE_HOURS, LIFE_AVAILABLE_HOURS_ARRAY, SCREEN_HEIGHT } from '../util/constants';

import SingleTab from './common/SingleTab';

const Last30DaysRoute = ({usage, timeSpan}) => <SingleTab usage={usage} timeSpan={timeSpan}/>

const ThisWeekRoute = ({usage, timeSpan}) => <SingleTab usage={usage} timeSpan={timeSpan}/>

const TodayRoute = ({usage, timeSpan}) => <SingleTab usage={usage} timeSpan={timeSpan}/>

const formatUsage = (usage) => {
  usage = usage/60; 

  if(usage >= 25) {

    //round off to 5
    usage = Math.round(usage/5)*5;

  } else if (usage >= 10) {

    //whole number
    usage = Math.round(usage);

  } else {

    //single decimal
    usage = Math.round(usage*10)/10;
  }

  return usage;
}

const sumReducer = (previousValue, currentValue) => previousValue + currentValue;

const Tabs = ({ width, dayOfTheWeek ,todayUsage, thisWeekUsage, last30DaysUsage}) => {

  const [index, setIndex] = useState(1);

  const [routes, setRoutes] = useState([
    { key: "today", title: "Today"},
    { key: "thisWeek", title: "This Week"},
    { key: "last30Days", title: "Last 30 Days"}
  ]);

  const customRenderScene = ({ route }) => {
    switch(route.key) {
      case "today":
        return <TodayRoute usage={formatUsage(todayUsage)} timeSpan={LIFE_AVAILABLE_HOURS_ARRAY[dayOfTheWeek]}/>
      case "thisWeek":
        return <ThisWeekRoute usage={formatUsage(thisWeekUsage)} timeSpan={LIFE_AVAILABLE_HOURS_ARRAY.slice(0, dayOfTheWeek + 1).reduce(sumReducer)}/>
      case "last30Days":
        return <Last30DaysRoute usage={formatUsage(last30DaysUsage)} timeSpan={30 * LIFE_AVAILABLE_HOURS}/>
      default:
        return <Text>Something went wrong</Text>
    }
  }

  return (
    <TabView
    style={styles.container}
    sceneContainerStyle={styles.scene}
    navigationState={{ index, routes}}
    renderScene={customRenderScene}
    onIndexChange={setIndex}
    initialLayout={{ width: width}}
    
    renderTabBar={props =>
    <TabBar
    {...props}
    indicatorStyle={{ backgroundColor: "black" }}
    style={{ backgroundColor: COLOR_2, padding: 0, opacity:0.9 }}
    tabStyle={{minHeight: SCREEN_HEIGHT * 0.05}}
    renderLabel={({ route, focused, color }) => (
      <Text style={{ color, margin: 0 }}>
          {route.title}
      </Text>
    )}
    />}
         
    />
  );

}

export default Tabs;

const styles = StyleSheet.create({
  container: {
    // backgroundColor: '#000',
    height: SCREEN_HEIGHT * 0.2,
    paddingTop:10
  },
  scene: {
    overflow: 'visible',
  }
});
