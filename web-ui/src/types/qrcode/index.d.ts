export type QRCodeErrorCorrectionLevel = 'low' | 'medium' | 'quartile' | 'high' | 'L' | 'M' | 'Q' | 'H';

export interface QRCodeOptions {
  version?: number | undefined;
  errorCorrectionLevel?: QRCodeErrorCorrectionLevel | undefined;
  toSJISFunc?: ((codePoint: string) => number) | undefined;
}

export interface QRCodeToDataURLOptions extends QRCodeRenderersOptions {
  type?: 'image/png' | 'image/jpeg' | 'image/webp' | undefined;
  rendererOpts?: {
    quality?: number | undefined;
  } | undefined;
}

export interface QRCodeToStringOptions extends QRCodeRenderersOptions {
  type?: 'utf8' | 'svg' | 'terminal' | undefined;
}

export interface QRCodeRenderersOptions extends QRCodeOptions {
  margin?: number | undefined;
  scale?: number | undefined;
  width?: number | undefined;
  color?: {
    dark?: string | undefined;
    light?: string | undefined;
  } | undefined;
}

export interface QRCodeSegment {
  data: string | Buffer | Uint8ClampedArray;
  mode: 'alphanumeric' | 'numeric' | 'kanji' | 'byte';
}

export interface QRCode {
  modules: any;
  version: number;
  errorCorrectionLevel: number;
  maskPattern: any;
  segments: QRCodeSegment[];
}

export function create(text: string | QRCodeSegment[], options: QRCodeOptions): QRCode;

export function toCanvas(
  canvasElement: HTMLCanvasElement,
  text: string | QRCodeSegment[],
  callback: (error: Error) => void,
): void;

export function toCanvas(
  canvasElement: HTMLCanvasElement,
  text: string | QRCodeSegment[],
  options?: QRCodeRenderersOptions,
): Promise<any>;

export function toCanvas(
  canvasElement: HTMLCanvasElement,
  text: string | QRCodeSegment[],
  options: QRCodeRenderersOptions,
  callback: (error: Error) => void,
): void;

export function toCanvas(
  text: string | QRCodeSegment[],
  callback: (error: Error, canvas: HTMLCanvasElement) => void,
): void;

export function toCanvas(text: string | QRCodeSegment[], options?: QRCodeRenderersOptions): Promise<any>;

export function toCanvas(
  text: string | QRCodeSegment[],
  options: QRCodeRenderersOptions,
  callback: (error: Error, canvas: HTMLCanvasElement) => void,
): void;

export function toCanvas(canvas: any, text: string | QRCodeSegment[], callback: (error: Error) => void): void;

export function toCanvas(canvas: any, text: string | QRCodeSegment[], options?: QRCodeRenderersOptions): Promise<any>;

export function toCanvas(
  canvas: any,
  text: string | QRCodeSegment[],
  options: QRCodeRenderersOptions,
  callback: (error: Error) => void,
): void;

export function toDataURL(
  canvasElement: HTMLCanvasElement,
  text: string | QRCodeSegment[],
  callback: (error: Error, url: string) => void,
): void;

export function toDataURL(
  canvasElement: HTMLCanvasElement,
  text: string | QRCodeSegment[],
  options?: QRCodeToDataURLOptions,
): Promise<string>;

export function toDataURL(
  canvasElement: HTMLCanvasElement,
  text: string | QRCodeSegment[],
  options: QRCodeToDataURLOptions,
  callback: (error: Error, url: string) => void,
): void;

export function toDataURL(text: string | QRCodeSegment[], callback: (error: Error, url: string) => void): void;

export function toDataURL(text: string | QRCodeSegment[], options?: QRCodeToDataURLOptions): Promise<string>;

export function toDataURL(
  text: string | QRCodeSegment[],
  options: QRCodeToDataURLOptions,
  callback: (error: Error, url: string) => void,
): void;

export function toString(text: string | QRCodeSegment[], callback: (error: Error, string: string) => void): void;

export function toString(text: string | QRCodeSegment[], options?: QRCodeToStringOptions): Promise<string>;

export function toString(
  text: string | QRCodeSegment[],
  options: QRCodeToStringOptions,
  callback: (error: Error, string: string) => void,
): void;
