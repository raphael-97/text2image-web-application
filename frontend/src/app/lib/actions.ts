"use server";

import { LoginRequest } from "../../dto/loginRequest";
import { RegisterRequest } from "../../dto/registerRequest";
import { redirect } from "next/navigation";
import { cookies } from "next/headers";
import { UserResponse } from "@/dto/userResponse";
import { TokenResponse } from "@/dto/tokenResponse";
import { RefreshTokenRequest } from "@/dto/refreshTokenRequest";
import { ModelRequest } from "@/dto/modelRequest";

const requestRegister = async (formData: FormData) => {
  const username = formData.get("username");
  const email = formData.get("email");
  const password = formData.get("password");

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
    throw new Error("Registration failed");
  }
  return response.json();
};

const requestLogin = async (formData: FormData) => {
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
    throw new Error("account does not exist");
  }
  return response.json();
};

export async function registerAction(formData: FormData) {
  try {
    const data = await requestRegister(formData);

    const tokenResponse: TokenResponse = {
      ...data,
    };

    await cookies().set({
      name: "accessToken",
      value: tokenResponse.accessToken,
      httpOnly: true,
      sameSite: true,
      secure: true,
    });

    await cookies().set({
      name: "refreshToken",
      value: tokenResponse.refreshToken,
      httpOnly: true,
      sameSite: true,
      secure: true,
    });
  } catch (error) {
    return "Registration failed";
  }
  await redirect("/gallery");
}

export async function loginAction(prevState: any, formData: FormData) {
  try {
    const data = await requestLogin(formData);

    const tokenResponse: TokenResponse = {
      ...data,
    };
    await cookies().set({
      name: "accessToken",
      value: tokenResponse.accessToken,
      httpOnly: true,
      sameSite: true,
      secure: true,
    });

    await cookies().set({
      name: "refreshToken",
      value: tokenResponse.refreshToken,
      httpOnly: true,
      sameSite: true,
      secure: true,
    });
  } catch (error) {
    return "Wrong credentials";
  }
  await redirect("/gallery");
}

export async function createModelAction(formData: FormData) {
  const modelRequest: ModelRequest = {
    name: formData.get("name"),
    inferenceUrl: formData.get("inferenceUrl"),
  };

  const sendFormData = new FormData();

  const file = formData.get("file");

  if (file instanceof File) {
    sendFormData.append("file", file, file.name);
  }

  sendFormData.append(
    "modelRequest",
    new Blob([JSON.stringify(modelRequest)], { type: "application/json" })
  );

  await fetch(`${process.env.NEXT_PUBLIC_BACKEND_API_URL}/api/models`, {
    headers: {
      Authorization: `Bearer ${await cookies().get("accessToken")?.value}`,
    },
    method: "POST",
    body: sendFormData,
  });

  await redirect("/explore");
}

export async function logOutAction() {
  cookies().delete("accessToken");
  cookies().delete("refreshToken");
  redirect("/");
}

export async function isLoggedIn() {
  if (cookies().get("accessToken") && cookies().get("refreshToken"))
    return true;
  return false;
}

export async function removeTokens() {
  cookies().delete("accessToken");
  cookies().delete("refreshToken");
  cookies().delete("JSESSION");
}

export async function fetchUserData() {
  return await fetch(`${process.env.NEXT_PUBLIC_BACKEND_API_URL}/api/user`, {
    headers: {
      Authorization: `Bearer ${await cookies().get("accessToken")?.value}`,
    },
  })
    .then((res) => {
      if (!res.ok) {
        return;
      }
      return res.json();
    })
    .then((data) => {
      const userResponse: UserResponse = {
        ...data,
      };
      return userResponse;
    });
}

export async function handleGoogleLogin() {
  await redirect(
    `${process.env.NEXT_PUBLIC_BACKEND_API_URL}/oauth2/authorization/google`
  );
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
