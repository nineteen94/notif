import React from "react";
import { BarChart } from "react-native-chart-kit";
import * as Constants from '../../util/constants';

const ApplicationCardChart = ({chartData}) => {

  return(
  
  <BarChart

    data={{
      labels: Constants.CHART_LABELS,
      datasets: [{data: chartData}]
    }}
    
    width={Constants.SCREEN_WIDTH * 0.85 + 50}
    height={Constants.SCREEN_WIDTH * 0.75}
    
    withHorizontalLabels={false}

    chartConfig={{
      backgroundGradientFrom: Constants.COLOR_CHART_1,
      backgroundGradientTo: Constants.COLOR_CHART_2,
      fillShadowGradient: Constants.COLOR_CHART_SHADOW,
      fillShadowGradientOpacity:1,
      decimalPlaces:0,
      barPercentage: 0.8,
      color: () => "black",
      formatYLabel:(y)=>{
        if(y!=0) y = y + " min";
        return y;
      },
      
    
    }}

    style={{
      marginLeft: -50
    }}
    
    withInnerLines={false}
    showValuesOnTopOfBars={true}
    segments={2}
    fromZero={true}
    showBarTops={false}
  />

  );
}

export default ApplicationCardChart;