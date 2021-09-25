import React from 'react';
import {View, Text, StyleSheet} from 'react-native';

const SingleTabBox = ({number, description, tooltip}) => {

  return(
    <View style={styles.container}>
      <Text numberOfLines={1} adjustsFontSizeToFit style={styles.numberStyle} >{number}</Text>
      <Text numberOfLines={1} adjustsFontSizeToFit style={styles.descriptionStyle}>{description}</Text>
    </View>
  );

}

export default SingleTabBox;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexDirection: "column",
    justifyContent: "space-around",
  },
  numberStyle: {
    flex: 0.7,
    alignSelf: "center",
    fontWeight: 'bold',
    fontStyle: 'italic',
    color: "black",
    fontSize: 50,
    fontFamily: "sans-serif-medium"
  },
  descriptionStyle: {
    alignSelf: "center",
    fontSize: 50,
    flex: 0.25,
    fontFamily: "sans-serif-light"
  }
});