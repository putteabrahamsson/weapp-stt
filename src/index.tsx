import {
  DeviceEventEmitter,
  NativeModules,
  PermissionsAndroid,
  Platform,
  type PermissionStatus,
} from 'react-native';
import type { SpeechEventListeners } from './types';

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

export const weappSTT = {
  addListener: (
    event: SpeechEventListeners,
    callback: (data: { value: string }) => void
  ) => {
    DeviceEventEmitter.addListener(event, callback);
  },
  removeListeners: () => {
    DeviceEventEmitter.removeAllListeners();
  },
};

export const isAvailable = async () => {
  return await WeappStt.isAvailable();
};

export const requestPermission = async (): Promise<PermissionStatus> => {
  return await PermissionsAndroid.request(
    PermissionsAndroid.PERMISSIONS.RECORD_AUDIO
  );
};
