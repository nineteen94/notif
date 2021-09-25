import React from "react";
import { Divider, DividerProps } from "react-native-elements/dist/divider/Divider";
import * as Constants from '../../util/constants';

const DividerSpace = ({width}) => {
  return (
    <Divider 
    orientation="horizontal" 
    width={width}
    color={Constants.COLOR_1}
    />
  );
}

export default DividerSpace;