import { Dimensions } from 'react-native';

export const APPMODEL_PACKAGENAME = "PACKAGENAME";
export const APPMODEL_APPNAME = "APPNAME";
export const APPMODEL_URI = "URI";
export const APPMODEL_HISTORICALUSAGE = "HISTORICALUSAGE";
export const APPMODEL_AVERAGEUSAGE = "AVERAGEUSAGE";
export const APPMODEL_LASTWEEKUSAGE = "LASTWEEKUSAGE";
export const APPMODEL_ISMONITORED = "ISMONITORED";
export const APPMODEL_WEEKDAYSUSAGE = "WEEKDAYSUSAGE";
export const APPMODEL_DAYOFTHEWEEK = "DAYOFTHEWEEK";
export const APPMODEL_THISWEEKUSAGE = "THISWEEKUSAGE";


export const DROPDOWN_NUMAPPS = 20;

export const CHART_LABELS = ["Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat"];

// export const CHART_LABELS = ["Sunday", "Monday", "Tueday", "Wednesday", "Thursday", "Friday", "Saturday"];

export const SCREEN_HEIGHT = Dimensions.get("window").height;
export const SCREEN_WIDTH = Dimensions.get("window").width;

export const RTL_DIRECTION = (rtl, style) => {
  const newStyle = {...style};

  if (rtl && ! I18nManager.isRTL) {
      if (newStyle.hasOwnProperty('flexDirection')) {
          newStyle['flexDirection'] = newStyle['flexDirection'] === 'row' ? 'row-reverse' : 'row';
      } else {
          newStyle['flexDirection'] = 'row-reverse';
      }
  }

  return newStyle;
}

export const RTL_STYLE = (rtl, style) => {
  const newStyle = {...style};

  if (rtl && ! I18nManager.isRTL) {
      Object.keys(style).map((key) => {
          if (STYLE_DIRECTION_KEYS.hasOwnProperty(key)) {
              newStyle[STYLE_DIRECTION_KEYS[key]] = newStyle[key];
              delete newStyle[key];
          } else {
              newStyle[key] = newStyle[key];
          }
      });
  }

  return newStyle;
}



// export const COLOR_4 = "#f594b7";
// export const COLOR_1 = "#f9fafe"; //main

export let COLOR_1 = "#ffffff"; //main

export let COLOR_2 = "#92b2fd"; //semi main
export let COLOR_3 = "#ad7ffb"; // B
export let COLOR_5 = "#ccd0f6"; // L


COLOR_1 = "#FEF9EF";
COLOR_1 = "#fcf9f3";

COLOR_3 = "#145DA0"; // app select button

COLOR_2 = "#2E8BC0"; // chart and tabs

COLOR_5 = "#B1D4E0"; // life tool tip




export const COLOR_BUTTON = COLOR_3;
export const COLOR_LIFE = COLOR_5;

export const COLOR_CHART_1 = COLOR_1;
export const COLOR_CHART_2 = COLOR_1;

export const COLOR_CHART_SHADOW = COLOR_2;











export const LIFE_WORK_HOURS = 8;
export const LIFE_SLEEP_HOURS = 8;
export const LIFE_ESSENTIAL_HOURS = 2;

const WEEKDAYS_HOURS = 24 - LIFE_WORK_HOURS - LIFE_ESSENTIAL_HOURS - LIFE_SLEEP_HOURS;
const WEEKENDS_HOURS = 24 - LIFE_ESSENTIAL_HOURS - LIFE_SLEEP_HOURS;

export const LIFE_AVAILABLE_HOURS = Math.round((WEEKDAYS_HOURS * 5 + WEEKENDS_HOURS * 2) / 7);

export const LIFE_AVAILABLE_HOURS_ARRAY = [
    WEEKENDS_HOURS, 
    WEEKDAYS_HOURS, WEEKDAYS_HOURS, WEEKDAYS_HOURS, WEEKDAYS_HOURS, WEEKDAYS_HOURS,
    WEEKENDS_HOURS
];



const APPNAME = "Dexter";

export const introScreenData = [
    {
        key: 1,
        title: "Hi 👋👋 I am " + APPNAME + "!!",
        text: "I help hoomans 👩‍🦱👨‍🦱 control their mobile📱 usage",
        // image:{uri: 'https://media4.giphy.com/media/SA613Nxg1h6zO1nRsg/giphy.gif?cid=790b76111763e6ea2811e680b3bc2bc527a644bcd4f5cf21&rid=giphy.gif&ct=g'},
        image: require('../assets/FirstSub.gif'),
        backgroundColor: {backgroundColor: '#59b2ab'}
    },

    {
        key: 2,
        title: "This is how I roll🧻🧻",
        text: "1️⃣ You choose the apps \n\n\n 2️⃣ I monitor🖥️ the app usage \n\n\n 3️⃣ I nag you when you don't behave 😈👻",
        // image: {uri: 'https://media1.giphy.com/media/FnsbzAybylCs8/giphy.webp?cid=ecf05e47fsqvzw517ca6k3enkqxht3230a0wihez2ly25gp2&rid=giphy.webp&ct=g'},
        image: require('../assets/Second.webp') ,
        backgroundColor: {backgroundColor: '#fbc850'}, //#febe29,
    },

    {
        key: 3,
        title: 'I need two permissions if we are going to work out 💌',
        text: " \n \n 1. To see mobile usage \n \n 2. To run all the time",
        // image: {uri: 'https://media.giphy.com/media/kiBcwEXegBTACmVOnE/giphy.gif'},
        // image: require('../assets/Third.webp') ,
        backgroundColor: {backgroundColor: '#22bcb5'},
        button1: "See mobile usage",
        button2: "🏃‍♀️Run all the time",
        button3: "You need to ignore batter optimization for me😊",
        permissionScreen: true
    }

];

export const PERMISSION_GIVEN = "✅";
export const PERMISSION_PENDING = "❗";

export const LIFETOOLTIPTEXT = `
An average hooman 👩‍🦱 ${"\n"} 
❌  Works for ${LIFE_WORK_HOURS} hours ${"\n"}
❌  Sleeps for ${LIFE_SLEEP_HOURS} hours ${"\n"}
❌  Eats, Shits, Other Essentials for ${LIFE_ESSENTIAL_HOURS} hours${"\n\n"}

✅  This leaves us ~${LIFE_AVAILABLE_HOURS} hours per day to live our life ♥`;