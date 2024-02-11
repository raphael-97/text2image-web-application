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
import { useState } from "react";
import { UserResponse } from "@/dto/userResponse";

export default function ThemeSwitcherAvatar(props: {
  isAuthorized: boolean;
  userData: UserResponse;
}) {
  const [isAuthorized, setIsAuthorized] = useState(props.isAuthorized);

  const [userData, setUserData] = useState<UserResponse>(props.userData);

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
              credits: {userData.credits}
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
