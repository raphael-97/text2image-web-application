"use server";

import { LoginRequest } from "@/dto/loginRequest";
import { RefreshTokenRequest } from "@/dto/refreshTokenRequest";
import { RegisterRequest } from "@/dto/registerRequest";
import { ResourceServerResponse } from "@/dto/resourceServerResponse";
import { TokenResponse } from "@/dto/tokenResponse";
import { cookies } from "next/headers";
import { redirect } from "next/navigation";

export async function registerAction(prevState: any, formData: FormData) {
  const username = formData.get("username");
  const email = formData.get("email");
  const password = formData.get("password");
  const confirmPassword = formData.get("confirmPassword");

  if (password !== confirmPassword) {
    return "Passwords not matching";
  }

  const registerData: RegisterRequest = {
    username: username,
    email: email,
    password: password,
  };

  const response = await fetch(
    `${process.env.NEXT_PUBLIC_BACKEND_API_URL}/api/auth/register`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(registerData),
    }
  );

  if (!response.ok) {
    const errorJson = await response.json();

    const errorResponse: ResourceServerResponse = {
      ...errorJson,
    };
    return errorResponse.message;
  }

  const data = await response.json();

  const tokenResponse: TokenResponse = {
    ...data,
  };

  setAuthCookies(tokenResponse);
  redirect(`${process.env.NEXT_PUBLIC_FRONTEND_DOMAIN_URL}/gallery`);
}

export async function loginAction(prevState: any, formData: FormData) {
  const email = formData.get("email");
  const password = formData.get("password");

  const loginData: LoginRequest = {
    email: email,
    password: password,
  };

  const response = await fetch(
    `${process.env.NEXT_PUBLIC_BACKEND_API_URL}/api/auth/login`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(loginData),
    }
  );

  if (!response.ok) {
    const errorJson = await response.json();
    const errorResponse: ResourceServerResponse = {
      ...errorJson,
    };
    return errorResponse.message;
  }
  const data = await response.json();
  const tokenResponse: TokenResponse = {
    ...data,
  };
  setAuthCookies(tokenResponse);
  redirect(`${process.env.NEXT_PUBLIC_FRONTEND_DOMAIN_URL}/gallery`);
}

export async function setAuthCookies(tokenResponse: TokenResponse) {
  cookies().set({
    name: "accessToken",
    value: tokenResponse.accessToken,
    httpOnly: true,
    sameSite: true,
    secure: true,
  });

  cookies().set({
    name: "refreshToken",
    value: tokenResponse.refreshToken,
    httpOnly: true,
    sameSite: true,
    secure: true,
  });
}

export async function getNewJwtAndRefreshToken(refreshTokenParam: string) {
  const refreshTokenRequest: RefreshTokenRequest = {
    refreshToken: refreshTokenParam,
  };

  return await fetch(
    `${process.env.NEXT_PUBLIC_BACKEND_API_URL}/api/auth/refreshtoken`,
    {
      headers: {
        "Content-Type": "application/json",
      },
      method: "POST",
      body: JSON.stringify(refreshTokenRequest),
    }
  )
    .then((response) => {
      if (!response.ok) {
        throw new Error("RefreshToken is expired!");
      }
      return response.json();
    })
    .then((data) => {
      const tokenResponse: TokenResponse = {
        ...data,
      };
      return tokenResponse;
    });
}

export async function logOutAction() {
  removeTokens();
  redirect(`${process.env.NEXT_PUBLIC_FRONTEND_DOMAIN_URL}`);
}

export async function removeTokens() {
  cookies().delete("accessToken");
  cookies().delete("refreshToken");
  cookies().delete("JSESSIONID");
}

export async function handleGoogleLogin() {
  redirect(
    `${process.env.NEXT_PUBLIC_BACKEND_DOMAIN_URL}/oauth2/authorization/google`
  );
}
