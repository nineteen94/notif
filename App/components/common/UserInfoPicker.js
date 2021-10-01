import React, { useEffect, useRef, useState } from 'react';
import { StyleSheet } from 'react-native';
import Picker, { PickerSelectProps } from 'react-native-picker-select';
import { useField } from '@unform/core';

const UserInfoPicker = ({ name, items, ...rest }) => {
  const pickerRef = useRef(null);
  const { fieldName, registerField, defaultValue = '' } = useField(name);

  const [selectedValue, setSelectedValue] = useState(defaultValue);

  useEffect(() => {
    registerField({
      name: fieldName,
      ref: pickerRef.current,
      getValue: ref => {
        return ref.props.value || '';
      },
      clearValue: ref => {
        ref.props.onValueChange(ref.props.placeholder.value);
      },
      setValue: (_, value) => {
        setSelectedValue(value);
      },
    });
  }, [fieldName, registerField]);

  return (
    <Picker
      ref={pickerRef}
      value={selectedValue}
      onValueChange={setSelectedValue}
      placeholder={{label: "Your pronoun", value: null}}
      items={items}
      style={{ inputAndroid: { color: '#000' } }}
      {...rest}
    />
  );

}

export default UserInfoPicker;

const styles = StyleSheet.create({
  pickerStyle: {
    borderWidth: 1,
    borderColor: 'gray',
    borderRadius: 4,
    paddingVertical: 16,
    // inputAndroid: { color: '#000' }
    "color": "#444"
  }
})