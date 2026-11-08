/** POST /api/v1/members/login */
export interface LoginRequest {
  loginId: string;
  password: string;
}

export interface LoginResponse {
  token: string;
}

/** POST /api/v1/members — CreateUserSignupRequest */
export interface SignupRequest {
  loginId: string;
  password: string;
  name: string;
  nickname: string;
  email: string;
}
