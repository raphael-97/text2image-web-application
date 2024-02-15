"use client";
import { Button, Input, Link, Divider } from "@nextui-org/react";
import { useFormState } from "react-dom";
import { loginAction } from "@/app/lib/authActions";
import SocialLoginComponent from "./SocialLoginComponent";

const messageInit = "";

export default function LoginForm() {
  const [errorMessage, action] = useFormState(loginAction, messageInit);
  return (
    <form action={action} className="px-5">
      <Input
        className="pt-5"
        label="Email"
        name="email"
        isRequired
        placeholder="Enter your email"
        type="email"
      />
      <Input
        className="pt-5"
        label="Password"
        name="password"
        isRequired
        placeholder="Enter your password"
        type="password"
      />
      <p className="text-center text-small pt-5">
        Need to create an account?{" "}
        <Link href="/register" size="sm">
          Sign up
        </Link>
      </p>
      <div className="pt-5">
        <Button type="submit" fullWidth color="primary">
          Log in
        </Button>
      </div>

      {errorMessage && (
        <div className="flex justify-center items-center">
          <p className="mt-5 text-danger">{errorMessage}</p>
        </div>
      )}

      <div className="flex">
        <Divider className="mt-6" />
      </div>
      <p className="text-center text-small pt-5">or continue with </p>
      <div className="flex flex-row items-center justify-center gap-3 mt-2">
        <SocialLoginComponent />
      </div>
    </form>
  );
}
