import React, {
  memo, useCallback, useMemo
} from 'react';

import {
  View,
  Text,
  TouchableOpacity
} from 'react-native';

import { RTL_DIRECTION, RTL_STYLE } from '../../util/constants';

// import {open, setOpen} from '../UsageScreen';
// import 

function RenderBadge({
  rtl,
  label,
  value,
  textStyle,
  badgeStyle,
  badgeTextStyle,
  badgeDotStyle,
  getBadgeColor,
  getBadgeDotColor,
  showBadgeDot,
  onPress,
  THEME,
  IconComponent,
}) {
  /**
   * onPress.
   */
  const __onPress = useCallback(() => onPress(value), [onPress, value]);

  // const onPressToggle = useCallback(() => setOpen(!open), [open, setOpen]);

  /**
   * The badge style.
   * @returns {object}
   */
  const _badgeStyle = useMemo(() => ([
      RTL_DIRECTION(rtl, THEME.badgeStyle),
      ...[badgeStyle].flat(), {
          backgroundColor: getBadgeColor(value)
      }
  ]), [THEME, rtl, badgeStyle, getBadgeColor]);

  /**
   * The badge dot style.
   * @return {object}
   */
  const _badgeDotStyle = useMemo(() => ([
      RTL_STYLE(rtl, THEME.badgeDotStyle),
      ...[badgeDotStyle].flat(), {
          backgroundColor: getBadgeDotColor(value)
      }
  ]), [THEME, rtl, badgeDotStyle, getBadgeDotColor]);

  /**
   * The badge text style.
   * @returns {object}
   */
  const _badgeTextStyle = useMemo(() => ([
      ...[textStyle].flat(),
      ...[badgeTextStyle].flat()
  ]), [textStyle, badgeTextStyle]);

  // console.log(IconComponent());

  return (
      <TouchableOpacity style={_badgeStyle}>
          {showBadgeDot && <View style={_badgeDotStyle} />}
          {IconComponent()}
          {/* <Text style={_badgeTextStyle}>{label}</Text> */}
      </TouchableOpacity>
  );
}

const areEqual = (nextProps, prevProps) => {
  if (nextProps.label !== prevProps.label)
      return false;
  if (nextProps.value !== prevProps.value)
      return false;
  if (nextProps.showBadgeDot !== prevProps.showBadgeDot)
      return false;
  if (nextProps.rtl !== prevProps.rtl)
      return false;
  if (nextProps.theme !== prevProps.theme)
      return false;

  return true;
}

export default memo(RenderBadge, areEqual);