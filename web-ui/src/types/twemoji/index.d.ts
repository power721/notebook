export type ParseCallback = (icon: string, options: object, variant: string) => string | false;

export interface ParseObject {
  callback: ParseCallback;
  attributes: (icon: string, variant: string) => object;
  base: string;
  ext: string;
  className: string;
  size: string | number;
  folder: string;
}

export interface Emoji {
  parse<T extends string | HTMLElement>(what: T, how?: Partial<ParseObject> | ParseCallback): T;
}
