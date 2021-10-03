import type { Review } from "@domains/Review";

export class Movie {
    constructor(
        readonly id: string,
        readonly title: string,
        private review: Review,
    ) {}

    reviewAverageWithFullScore(): string {
        return this.review.averageWithFullScore()
    }
}