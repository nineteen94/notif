
export const getTimeStamps = (time) => {
  if(time === 0) return [0,0,0];
  const date = new Date(time);
  date.setSeconds(0); date.setMinutes(0); date.setHours(0); date.setMilliseconds(0);
  const fetchWeek = date.getTime();
  const dayOfTheWeek = date.getDay();
  date.setDate(date.getDate() - date.getDay());
  const fetchHistory = date.getTime();
  return [fetchHistory, fetchWeek, dayOfTheWeek];
}

export const toastMaker = (type, title, message) => {
  Toast.show({
    type: type,
    text1: title,
    text2: message
  });
}

export const getMinutes = (time) => {
  const date = new Date(time);
  const min = date.getMinutes();
  const hours = date.getHours();
  return (hours*60+min);
}


