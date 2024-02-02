"use client";
import {
  Avatar,
  Button,
  Dropdown,
  DropdownItem,
  DropdownMenu,
  DropdownTrigger,
  Link,
  NavbarContent,
} from "@nextui-org/react";

import { ThemeSwitcher } from "../ThemeSwitcher";
import { getUserData, isLoggedIn, logOutAction } from "@/app/lib/actions";
import { useEffect, useState } from "react";
import { UserDto } from "@/dto/userDto";

export default function ThemeSwitcherAvatar(props: { isAuthorized: boolean }) {
  const [isAuthorized, setIsAuthorized] = useState(props.isAuthorized);

  const initUserData: UserDto = {
    username: "",
    email: "",
    credits: 0,
  };

  const [userData, setUserData] = useState<UserDto>(initUserData);

  useEffect(() => {
    const getUserLoggedin = async () => {
      const isUserLoggedIn = await isLoggedIn();
      setIsAuthorized(isUserLoggedIn);
      if (isUserLoggedIn) {
        const userDataResponse: UserDto = await getUserData();
        setUserData(userDataResponse);
      }
    };
    getUserLoggedin();
  }, [props.isAuthorized]);

  return (
    <NavbarContent justify="end">
      <ThemeSwitcher />
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
              <p className="font-semibold">{userData.email}</p>
            </DropdownItem>
            <DropdownItem
              onClick={() => {
                logOutAction();
                setIsAuthorized(false);
              }}
              key="logout"
              color="danger"
            >
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
  );
}
