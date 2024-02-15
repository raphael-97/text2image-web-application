"use client";
import { registerAction } from "@/app/lib/authActions";
import { Button, Input, Link, Divider } from "@nextui-org/react";
import SocialLoginComponent from "./SocialLoginComponent";
import { useFormState } from "react-dom";

const messageInit = "";

export default function RegisterForm() {
  const [errorMessage, action] = useFormState(registerAction, messageInit);
  return (
    <form action={action} className="px-5">
      <Input
        className="pt-5"
        label="Name"
        name="username"
        isRequired
        placeholder="Enter your username"
        type="text"
      />
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
      <Input
        className="pt-5"
        label="Password"
        name="confirmPassword"
        isRequired
        placeholder="Confirm your password"
        type="password"
      />
      <p className="text-center text-small pt-5">
        Already have an account?{" "}
        <Link href="/login" size="sm">
          Login
        </Link>
      </p>
      <div className="pt-5">
        <Button type="submit" fullWidth color="primary">
          Sign up
        </Button>
      </div>
      {errorMessage && (
        <div className="flex justify-center items-center">
          <p className="mt-5 text-danger">{errorMessage}</p>
        </div>
      )}

      <div className="flex">
        <Divider className="mt-8" />
      </div>
      <p className="text-center text-small pt-5">or continue with </p>
      <div className="flex flex-row items-center justify-center gap-3 mt-2">
        <SocialLoginComponent />
      </div>
    </form>
  );
}
