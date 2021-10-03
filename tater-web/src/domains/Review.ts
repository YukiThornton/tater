export class Review {
    constructor(private average: number) {}

    averageWithFullScore(): string {
        return `${this.average}/10`;
    }
}