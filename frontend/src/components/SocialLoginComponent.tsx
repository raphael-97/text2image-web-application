import { handleGoogleLogin } from "@/app/lib/authActions";
import { Link } from "@nextui-org/react";
import Image from "next/image";
import React from "react";

export default function SocialLoginComponent() {
  return (
    <Link onClick={() => handleGoogleLogin()}>
      <Image
        className="dark:hidden"
        src="web_light_rd_na.svg"
        alt="Google Logo"
        width={40}
        height={40}
      />
      <Image
        className="hidden dark:flex"
        src="web_dark_rd_na.svg"
        alt="Google Logo"
        width={40}
        height={40}
      />
    </Link>
  );
}
