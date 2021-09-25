import React from "react";
import {TouchableWithoutFeedback, StyleSheet, Image, View} from 'react-native';
import {Text} from 'react-native-elements';
import * as Constants from '../../util/constants';

const NoAppSelected = ({ closeDropDown }) => {

  return (
    <TouchableWithoutFeedback
    onPress={closeDropDown}>
    <View
    style={styles.container}>

      <Image
      source={{uri: 'https://media1.giphy.com/media/xUNda6pedTN07sbnQ4/giphy.gif?cid=790b7611a6625bc0083f5ef196023eb86200255c88d0c0d5&rid=giphy.gif&ct=g'}}
      style={styles.meme}/>
    
      <Text h3> Looks whats that????</Text>
      <Text h4> Nothing's there!!!</Text>
    </View>
    </TouchableWithoutFeedback>
  );

}

const styles = StyleSheet.create({

  container: {
    alignContent:"center",
    height: Constants.SCREEN_HEIGHT,
    // justifyContent:"center",
    alignSelf:"center"
  },
  meme: {
    width:Constants.SCREEN_WIDTH*0.8, 
    height:Constants.SCREEN_HEIGHT*0.4
  },

});

export default NoAppSelected;