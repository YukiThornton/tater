export default class Page {
    public static readonly Top = new Page(true);
    public static readonly Login = new Page(false);
    
    constructor(
        readonly authRequired: boolean
    ) {}
}