import React from "react";
import {Card} from 'react-native-elements';
import {StyleSheet} from 'react-native';
import { APPMODEL_APPNAME, APPMODEL_URI, APPMODEL_WEEKDAYSUSAGE} from "../../util/constants";

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
    borderBottomWidth: 0,
    shadowColor: "#ffffff" // background color
  }
});