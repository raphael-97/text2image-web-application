"use client";

import {
  Avatar,
  Button,
  Dropdown,
  DropdownItem,
  DropdownMenu,
  DropdownTrigger,
  Input,
  Link,
  Navbar,
  NavbarBrand,
  NavbarContent,
  NavbarItem,
  NavbarMenu,
  NavbarMenuItem,
  NavbarMenuToggle,
} from "@nextui-org/react";
import { FcSearch } from "react-icons/fc";
import { ThemeSwitcher } from "./ThemeSwitcher";
import { AiOutlineHome } from "react-icons/ai";
import { GrGallery } from "react-icons/gr";
import { RxHamburgerMenu } from "react-icons/rx";
import { PiAlien } from "react-icons/pi";
import { useState } from "react";

export const Navigationbar = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [isHomeActive, setIsHomeActive] = useState(true);
  const [isGalleryActive, setIsGalleryActive] = useState(false);
  const [isModelsActive, setIsModelsActive] = useState(false);
  const [isAuthorized, setIsAuthorized] = useState(false);

  const menuItems = [
    {
      name: "Home",
      icon: <AiOutlineHome />,
      link: "/",
      active: isHomeActive,
    },
    {
      name: "Models",
      icon: <PiAlien />,
      link: "models",
      active: isModelsActive,
    },
    {
      name: "Gallery",
      icon: <GrGallery />,
      link: "gallery",
      active: isGalleryActive,
    },
  ];

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
          "after:bg-secondary",
          "after:duration-300",

          "data-[active=true]:after:w-full",
          "data-[active=true]:after:h-[2px]",
          "data-[active=true]:after:bg-secondary",
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

      <NavbarMenu className="gap-4">
        {menuItems.map((item, index) => (
          <NavbarMenuItem
            isActive={item.active}
            key={`NavbarMenuItem_${index}`}
          >
            <Link
              className="gap-1"
              color={item.active ? "secondary" : "foreground"}
              href={item.link}
            >
              {item.icon} {item.name}{" "}
            </Link>
          </NavbarMenuItem>
        ))}
      </NavbarMenu>

      <NavbarContent className="hidden md:flex gap-16" justify="center">
        {menuItems.map((item, index) => (
          <NavbarItem isActive={item.active} key={`NavbarItem_${index}`}>
            <Link
              className="gap-1"
              color={item.active ? "secondary" : "foreground"}
              href={item.link}
            >
              {item.icon} {item.name}
            </Link>
          </NavbarItem>
        ))}

        <Input
          classNames={{
            base: "max-w-full sm:max-w-[10rem] h-10",
            mainWrapper: "h-full",
            input: "text-small",
            inputWrapper:
              "h-full font-normal text-default-500 bg-default-400/20 dark:bg-default-500/20",
          }}
          placeholder="Search"
          size="sm"
          startContent={<FcSearch />}
          type="search"
        />
      </NavbarContent>

      <NavbarContent justify="end">
        <ThemeSwitcher></ThemeSwitcher>
        {isAuthorized ? (
          <Dropdown placement="bottom-end">
            <DropdownTrigger>
              <Avatar
                isBordered
                as="button"
                className="transition-transform"
                color="secondary"
                size="sm"
                showFallback
              />
            </DropdownTrigger>
            <DropdownMenu aria-label="Profile Actions" variant="flat">
              <DropdownItem key="profile" className="h-14 gap-2">
                <p className="font-semibold">Signed in as</p>
                <p className="font-semibold">johnny@example.com</p>
              </DropdownItem>
              <DropdownItem href="/settings" key="settings">
                My Settings
              </DropdownItem>
              <DropdownItem key="logout" color="danger">
                Log Out
              </DropdownItem>
            </DropdownMenu>
          </Dropdown>
        ) : (
          <Button as={Link} color="primary" href="/register" variant="flat">
            Sign Up
          </Button>
        )}
      </NavbarContent>
    </Navbar>
  );
};
