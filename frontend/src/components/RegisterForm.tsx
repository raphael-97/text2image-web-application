"use client";
import { handleGoogleLogin, registerAction } from "@/app/lib/actions";
import { Button, Input, Link, Divider } from "@nextui-org/react";

export default function RegisterForm() {
  return (
    <form action={registerAction} className="px-5">
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

      <div className="flex">
        <Divider className="mt-8" />
      </div>
      <p className="text-center text-small pt-5">or continue with </p>
      <div className="flex flex-row items-center justify-center gap-3 mt-2">
        <Link onClick={() => handleGoogleLogin()}>
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
