import ApiClient from "./drivers/ApiClient";
import MovieRepository from "./repositories/MovieRepository";
import PagePresenter from "./presenters/PagePresenter";
import AuthPresenter from "./presenters/AuthPresenter";
import UserPresenter from "./presenters/UserPresenter";
import LoginPagePresenter from "./presenters/LoginPagePresenter";
import MoviePresenter from "./presenters/MoviePresenter";
import AppInitUseCase from "./usecases/AppInitUseCase";
import AuthUseCase from "./usecases/AuthUseCase";
import LoginUseCase from "./usecases/LoginUseCase";
import MovieListUseCase from "./usecases/MovieListUseCase";
import AppController from "./controllers/AppController";
import AuthController from "./controllers/AuthController";
import LoginController from "./controllers/LoginController";
import MovieListController from "./controllers/MovieListController";

import { 
    login as loginStore,
    top as topStore,
} from "@stores/routes"
import { authenticated as authenticatedStore } from "@stores/auth";
import { userId as userIdStore } from "@stores/user";
import { movies as movieStore } from "@stores/movie";
import { errorMessage as errorMessageStore } from "@stores/pages/login";

const apiClient = new ApiClient();

const movieRepo = new MovieRepository(apiClient);

const pagePresenter = new PagePresenter(loginStore, topStore);
const authPresenter = new AuthPresenter(authenticatedStore)
const userPresenter = new UserPresenter(userIdStore);
const moviePresenter = new MoviePresenter(movieStore);
const loginPagePresenter = new LoginPagePresenter(errorMessageStore);

const appInitUseCase = new AppInitUseCase(pagePresenter);
const authUseCase = new AuthUseCase(authPresenter, pagePresenter);
const loginUseCase = new LoginUseCase(authPresenter, userPresenter, pagePresenter, loginPagePresenter);
const movieListUseCase = new MovieListUseCase(movieRepo, moviePresenter, userPresenter);

export const appController = new AppController(appInitUseCase);
export const authController = new AuthController(authUseCase);
export const loginController = new LoginController(loginUseCase);
export const movieListController = new MovieListController(movieListUseCase);