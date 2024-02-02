"use server";

import { LoginDto } from "../../dto/loginDto";
import { RegisterDto } from "../../dto/registerDto";
import { redirect } from "next/navigation";
import { cookies } from "next/headers";
import { UserDto } from "@/dto/userDto";
import { tokenDto } from "@/dto/tokenDto";

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

  if (!response.ok) {
    throw new Error("Registration failed");
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

  if (!response.ok) {
    throw new Error("account does not exist");
  }
  return response.json();
};

export async function registerAction(formData: FormData) {
  try {
    const data = await requestRegister(formData);

    const userDto: tokenDto = {
      ...data,
    };

    cookies().set({
      name: "token",
      value: userDto.jwtToken,
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

    const userDto: tokenDto = {
      ...data,
    };
    cookies().set({
      name: "token",
      value: userDto.jwtToken,
      httpOnly: true,
      sameSite: true,
      secure: true,
    });
  } catch (error) {
    return "Wrong credentials";
  }
  await redirect("/gallery");
}

export async function logOutAction() {
  cookies().delete("token");
  await redirect("/");
}

export async function isLoggedIn() {
  if (cookies().get("token")) return true;
  return false;
}

export async function getUserData() {
  return await fetch(`${process.env.NEXT_PUBLIC_BACKEND_API_URL}/api/user`, {
    headers: {
      Authorization: `Bearer ${cookies().get("token")?.value}`,
    },
  })
    .then((res) => {
      if (!res.ok) {
        throw new Error("Fetching User Data failed");
      }
      return res.json();
    })
    .then((resData) => {
      const userData: UserDto = {
        ...resData,
      };
      return userData;
    });
}
