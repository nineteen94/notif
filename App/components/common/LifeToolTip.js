import React, {useState, useEffect} from 'react';
import {View, Text, StyleSheet} from 'react-native';
import { Tooltip } from 'react-native-elements';
import * as Constants from '../../util/constants';

const LifeToolTip = () => {

  return (
    <View style={styles.continer}>
    <Tooltip
    backgroundColor={Constants.COLOR_5}
    skipAndroidStatusBar={true}
    height={Constants.SCREEN_WIDTH * 0.7}
    width={Constants.SCREEN_WIDTH * 0.7}
    popover={<Text adjustsFontSizeToFit style={styles.tooltipText}>{Constants.LIFETOOLTIPTEXT}</Text>}
    >
      <Text style={styles.textStyle}>How we calculate🧮life spent on apps❓</Text>
    </Tooltip>
    </View>
  );

}

export default LifeToolTip;

const styles = StyleSheet.create({
  tooltipText: {
    color: "black",
    fontSize: 15,
    alignSelf: "auto"
  },
  continer: {
    backgroundColor: Constants.COLOR_5,
    padding: 2,
    alignItems: "center"
  },
  textStyle: {
    fontStyle: "italic",
  }

});