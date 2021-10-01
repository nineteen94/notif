import React from "react";
import { StyleSheet } from "react-native";
import { ListItem, Avatar } from "react-native-elements";
import * as Constants from '../../util/constants';


const ApplicationCardHead = ({uri, appName}) => {
  return(
    <ListItem 
    containerStyle={{backgroundColor: Constants.COLOR_1}}
    >
      <Avatar rounded source={{uri: uri}} />
      <ListItem.Content style={{backgroundColor: Constants.COLOR_1}}>
        <ListItem.Title style={styles.textStyle} >{appName} ⌛ minutes</ListItem.Title>
      </ListItem.Content>
    </ListItem>
  );
}

export default ApplicationCardHead;

const styles = StyleSheet.create({
  textStyle: {
    fontWeight: "bold"
  }
});