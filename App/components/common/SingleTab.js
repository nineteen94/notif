import React, { useEffect } from 'react';
import {View, Text, StyleSheet} from 'react-native';

import SingleTabBox from './SingleTabBox';

const formatLifePercentage = (usage, timeSpan) => {

  let lifePercent = usage * 100 / timeSpan;

  if(lifePercent >= 25) {
    lifePercent = Math.round(lifePercent/5)*5 + "%";
  } else if(lifePercent >= 10) {
    lifePercent = Math.round(lifePercent) + "%";
  } else {
    lifePercent = "0" + Math.round(lifePercent) + "%";
  }

  return lifePercent;
}

const SingleTab = ({usage, timeSpan}) => {

  return(
    <View style={styles.container}>
      <SingleTabBox number={usage} description="Hours"/>
      <SingleTabBox number={formatLifePercentage(usage, timeSpan)} description="Life" tooltip={true}/>
    </View>
  )
}

export default SingleTab;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexDirection: "row",
    justifyContent: "center",
    alignItems: "center"
  },
}) 