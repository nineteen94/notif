import React from "react";
import {Card} from 'react-native-elements';
import {StyleSheet} from 'react-native';
import { APPMODEL_APPNAME, APPMODEL_URI, APPMODEL_WEEKDAYSUSAGE, COLOR_1} from "../../util/constants";

import ApplicationCardChart from "./ApplicationCardChart";
import ApplicationCardHead from "./ApplicationCardHead";


const ApplicationCard = (props) => {

  return (
    <Card containerStyle={styles.cardContainer}>
      
      <ApplicationCardHead 
      uri={props.appObject.get(APPMODEL_URI)}
      appName={props.appObject.get(APPMODEL_APPNAME)}
      />

      <ApplicationCardChart 
      chartData={props.appObject.get(APPMODEL_WEEKDAYSUSAGE)}/>

    </Card>
  );

}

export default ApplicationCard;

const styles = StyleSheet.create({
  cardContainer: {
    paddingBottom:10 , 
    paddingTop:0 ,
    alignSelf: "center",
    alignContent: "center",
    borderLeftWidth: 0,
    borderRightWidth: 0,
    borderTopWidth: 0,
    borderBottomWidth: 2,
    shadowColor: COLOR_1, // background color,
    borderColor: "#ffffff",
    backgroundColor: COLOR_1

  }
});