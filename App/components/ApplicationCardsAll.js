import React from "react";
import { TouchableWithoutFeedback, View } from "react-native";
import ApplicationCard from "./common/ApplicationCard";

const ApplicationCardsAll = ({value, applicationCardMaps }) => {

  return (
    <View style={{zIndex: 1}} >
    {value.map(packageName => (
      <ApplicationCard 
      key={packageName} 
      appObject={applicationCardMaps.current.get(packageName)} />
    ))}
    </View>
  );

}

export default ApplicationCardsAll;