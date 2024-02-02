"use client";
import { Button, Input, Link, Divider } from "@nextui-org/react";
import { loginAction } from "@/app/lib/actions";
import { useFormState, useFormStatus } from "react-dom";

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
        <Button type="submit" fullWidth color="secondary">
          Log in
        </Button>
        {errorMessage && <p>{errorMessage}</p>}
      </div>

      <div className="flex">
        <Divider className="mt-8" />
      </div>
      <p className="text-center text-small pt-5">or continue with </p>
      <div className="flex flex-row items-center justify-center gap-3 mt-2">
        <Link href="/">
          <img
            className="dark:hidden"
            src="web_light_rd_na.svg"
            alt="Google Logo"
          />
          <img
            className="hidden dark:flex"
            src="web_dark_rd_na.svg"
            alt="Google Logo"
          />
        </Link>
      </div>
    </form>
  );
}
