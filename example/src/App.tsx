import { Text, View, StyleSheet } from 'react-native';
import { useState } from 'react';

export default function App() {
  const [result] = useState<number | undefined>();

  return (
    <View style={styles.container}>
      <Text>Result: {result}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
