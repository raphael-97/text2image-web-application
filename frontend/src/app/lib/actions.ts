"use server";

import { LoginDto } from "../dto/loginDto";
import { RegisterDto } from "../dto/registerDto";

const requestRegister = async (formData: FormData) => {
  const username = formData.get("username");
  const email = formData.get("email");
  const password = formData.get("password");

  const registerData: RegisterDto = {
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

  if (!(response.status === 201)) {
    return "Registration failed";
  }
  return response.json();
};

const requestLogin = async (formData: FormData) => {
  const email = formData.get("email");
  const password = formData.get("password");

  const loginData: LoginDto = {
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

  if (!(response.status === 202)) {
    return "account does not exist";
  }
  return response.json();
};

export async function registerAction(formData: FormData) {
  const data = await requestRegister(formData);
  console.log(data);
}

export async function loginAction(formData: FormData) {
  const data = await requestLogin(formData);
  console.log(data);
}
