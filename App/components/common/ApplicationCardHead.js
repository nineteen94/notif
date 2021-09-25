import React from "react";
import { StyleSheet } from "react-native";
import { ListItem, Avatar } from "react-native-elements";


const ApplicationCardHead = ({uri, appName}) => {
  return(
    <ListItem>
    <Avatar rounded source={{uri: uri}} />
    <ListItem.Content>
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