"use client";

import { Navbar, NavbarBrand, NavbarMenuToggle } from "@nextui-org/react";

import { RxHamburgerMenu } from "react-icons/rx";
import { useState } from "react";
import Menuitems from "./Menuitems";
import ThemeSwitcherAvatar from "./ThemeSwitcherAvatar";
import { UserResponse } from "@/dto/userResponse";

export const Navigationbar = (props: {
  isAuthorized: boolean;
  userData: UserResponse;
}) => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  return (
    <Navbar
      maxWidth="full"
      onMenuOpenChange={setIsMenuOpen}
      isBordered
      isBlurred
      classNames={{
        item: [
          "flex",
          "relative",
          "h-full",
          "items-center",
          "after:content-['']",
          "after:absolute",
          "after:bottom-0",
          "after:left-0",
          "after:right-0",

          "after:w-0",
          "hover:after:w-full",
          "hover:after:h-[2px]",
          "after:bg-primary",
          "after:duration-300",

          "data-[active=true]:after:w-full",
          "data-[active=true]:after:h-[2px]",
          "data-[active=true]:after:bg-primary",
          "data-[active=true]:after:duration-0",
        ],
      }}
    >
      <NavbarMenuToggle
        className="md:hidden"
        icon={
          <RxHamburgerMenu
            size={25}
            className="scale cursor-pointer"
          ></RxHamburgerMenu>
        }
      ></NavbarMenuToggle>

      <NavbarBrand className="d-sm-none d-md-block">
        <p className="font-bold">Text2Image</p>
      </NavbarBrand>

      <Menuitems />

      <ThemeSwitcherAvatar
        isAuthorized={props.isAuthorized}
        userData={props.userData}
      />
    </Navbar>
  );
};
