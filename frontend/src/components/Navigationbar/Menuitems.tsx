"use client";
import {
  Input,
  Link,
  NavbarContent,
  NavbarItem,
  NavbarMenu,
  NavbarMenuItem,
} from "@nextui-org/react";
import { AiOutlineHome } from "react-icons/ai";
import { GrGallery } from "react-icons/gr";
import { PiAlien } from "react-icons/pi";
import { FcSearch } from "react-icons/fc";
import { usePathname } from "next/navigation";

export default function Menuitems() {
  const pathname = usePathname();

  const menuItems = [
    {
      name: "Home",
      icon: <AiOutlineHome />,
      link: "/",
      active: pathname === "/",
    },
    {
      name: "Explore",
      icon: <PiAlien />,
      link: "/explore",
      active: pathname === "/explore",
    },
    {
      name: "Gallery",
      icon: <GrGallery />,
      link: "/gallery",
      active: pathname === "/gallery",
    },
  ];

  return (
    <>
      <NavbarContent className="hidden md:flex gap-16" justify="center">
        {menuItems.map((item, index) => (
          <NavbarItem isActive={item.active} key={`NavbarItem_${index}`}>
            <Link
              className="gap-1"
              color={item.active ? "primary" : "foreground"}
              href={item.link}
            >
              {item.icon} {item.name}
            </Link>
          </NavbarItem>
        ))}
      </NavbarContent>
      <NavbarMenu className="gap-4">
        {menuItems.map((item, index) => (
          <NavbarMenuItem
            isActive={item.active}
            key={`NavbarMenuItem_${index}`}
          >
            <Link
              className="gap-1"
              color={item.active ? "primary" : "foreground"}
              href={item.link}
            >
              {item.icon} {item.name}{" "}
            </Link>
          </NavbarMenuItem>
        ))}
      </NavbarMenu>
    </>
  );
}
