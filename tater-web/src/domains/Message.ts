export class Message {
    constructor(readonly text: string) {}
}

export const messages = {
    invalidLoginId: new Message('Invalid ID')
}