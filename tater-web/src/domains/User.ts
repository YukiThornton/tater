import type { TextInput } from "./Input";

export class UserId {
    constructor(readonly id: string) {}
    
    static isValid(text: TextInput): boolean {
        const value = Number(text.value)
        return !Number.isNaN(value)
    }
    
    static fromInput(text: TextInput): UserId {
        if (!UserId.isValid(text)) throw new Error(`Invalid userId: ${text.value}`);
        return new UserId(text.value);
    }
}
