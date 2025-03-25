import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'weapp-stt' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const WeappStt = NativeModules.WeappStt
  ? NativeModules.WeappStt
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export const startListening = (): Promise<void> => {
  return WeappStt.startListening();
};

export const stopListening = (): Promise<void> => {
  return WeappStt.stopListening();
};

export const destroy = (): Promise<void> => {
  return WeappStt.destroy();
};

export const setLanguage = (language: string): Promise<void> => {
  return WeappStt.setLanguage(language);
};

export const setTotalListeningLength = (
  milliseconds: number
): Promise<void> => {
  return WeappStt.setTotalListeningLength(milliseconds);
};

export const setListeningPauseLength = (
  milliseconds: number
): Promise<void> => {
  return WeappStt.setListeningPauseLength(milliseconds);
};
