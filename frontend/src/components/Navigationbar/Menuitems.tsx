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
      name: "Models",
      icon: <PiAlien />,
      link: "/models",
      active: pathname === "/models",
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
    </>
  );
}
