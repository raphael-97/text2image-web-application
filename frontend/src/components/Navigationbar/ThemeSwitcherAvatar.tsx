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
import { logOutAction } from "@/app/lib/actions";
import { useEffect, useState } from "react";
import { UserResponse } from "@/dto/userResponse";

export default function ThemeSwitcherAvatar(props: { isAuthorized: boolean }) {
  const [dropDownOpen, setDropDownOpen] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(props.isAuthorized);
  const [userData, setUserData] = useState<UserResponse>({
    username: "",
    email: "",
    credits: 0,
  });

  useEffect(() => {
    setIsLoggedIn(props.isAuthorized);
  }, [props.isAuthorized]);

  const fetchUserData = async (isOpen: boolean) => {
    if (isOpen) {
      const latestUserData = await fetch("/api/users");

      if (latestUserData.ok) {
        const data = await latestUserData.json();
        console.log(data);
        const userResponse: UserResponse = {
          ...data,
        };

        console.log(userResponse);

        await setUserData(userResponse);
        setDropDownOpen(true);
      }
    }
  };

  return (
    <NavbarContent justify="end">
      <ThemeSwitcher />
      {isLoggedIn ? (
        <Dropdown
          placement="bottom-end"
          onOpenChange={(isOpen) => fetchUserData(isOpen)}
          isOpen={dropDownOpen}
          onClose={() => setDropDownOpen(false)}
        >
          <DropdownTrigger>
            <Avatar
              isBordered
              as="button"
              className="transition-transform"
              color="primary"
              size="sm"
              showFallback
            />
          </DropdownTrigger>
          <DropdownMenu aria-label="Profile Actions" variant="flat">
            <DropdownItem key="profile" className="h-14 gap-2">
              <p className="font-semibold">Signed in as</p>
              <p className="font-semibold">{userData.email}</p>
            </DropdownItem>
            <DropdownItem key="credits">
              Credits: {userData.credits}
            </DropdownItem>
            <DropdownItem
              onClick={() => {
                setIsLoggedIn(false);
                logOutAction();
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
